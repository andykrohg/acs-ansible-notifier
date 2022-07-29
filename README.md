# ACS Ansible Notifier
This app is intended to serve as an intermediary between a Stackrox Generic Webhook notifier in Advanced Cluster Security and an instance of Ansible Automation Controller. It's useful in the event that you wish to run an Ansible job in response to an alert from ACS, such as a policy violation.

## Deployment Guide
### Configure Ansible Automation Controller
#### Create a Job Template
Ensure that you have a Job Template ready to roll on Automation Controller - we'll be invoking this upon policy violations using the REST API. You'll have access to contextual information in an extra variable called `alert`, so make sure your Template indicates **Prompt on launch** for extra variables. Make note of this template's *numerical template id*, viewable in the address bar.

Take a look at the [samples directory](samples) for some example use cases and corresponding playbooks.

#### Configure your instance group
If you want your ansible job to perform actions in OpenShift with the `k8s` module to respond to the policy violation, _and_ you have AAP deployed on OpenShift, you'll probably want to configure your runner `Pod` template with a service account that has the necessary privileges. In order to to this:
* Go to **Administration -> Instance Groups** in Automation Controller, and edit the **default** group. 
* Under Options, select **Customize pod specification**
* In the Custom pod spec, use a custom `serviceAccountName` and set `automountServiceAccountToken` to `true`:
  ```bash
  apiVersion: v1
  kind: Pod
  metadata:
    namespace: ansible-automation-platform
  spec:
    serviceAccountName: my-ansible-sa
    automountServiceAccountToken: true
    ...
  ```
* Then create that `ServiceAccount` and grant it privileges to do whatever you need it to. For example:
  ```bash
  oc -n ansible-automation-platform create serviceaccount my-ansible-sa
  oc -n ansible-automation-platform adm policy add-cluster-role-to-user cluster-admin -z my-ansible-sa
  ```

### Deploy the Notifier
Next, we'll deploy an instance of this project to OpenShift.
```bash
# Set environment variable for server URL. Be sure to include /api
ANSIBLE_API_SERVER_URL=https://automation-controller.com/api

oc new-app quay.io/akrohg/acs-ansible-notifier \
    -e ANSIBLE_API_SERVER_URL=$ANSIBLE_API_SERVER_URL

oc expose svc/acs-ansible-notifier
```

### Create an Integration spec on ACS
* In ACS, go to **Platform Configuration -> Integrations**
* Select **Notifier Integrations -> Generic Webhook**, and click **New Integration**
* Give your integration a name, and provide the route to your `acs-ansible-notifier` app in the **Endpoint**
* (Optional) Select **Skip TLS verification** if your Automation Controller isn't using a well-known certificate
* (Optional) Deselect **Enable audit logging** if all you want to do is respond to alerts
* Provide a **Username** and **Password** to authenticate to Automation Controller
* Under **Extra Fields**, add one that looks like this:
    * Key: `template_id`
    * Value: `<your numerical ansible job template id>`
* Then edit the Policy you want to notify on, and add this integration to the list of notifiers

## Local Development
### Running the quarkus app in dev mode
```bash
./mvnw quarkus:dev
```

### Building a container image
```bash
podman build . -f src/main/docker/Dockerfile.jvm -t local/acs-ansible-notifier --no-cache --platform linux/amd64
```