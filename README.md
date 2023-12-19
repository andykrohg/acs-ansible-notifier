# ACS Ansible Notifier
This repo houses example playbooks to facilitate integration use cases between Advanced Cluster Security and an instance of Ansible Automation Controller using Event-Driven Ansible. It's useful in the event that you wish to run an Ansible job in response to an alert from ACS, such as a policy violation.

## Deployment Guide
### Configure Ansible Automation Controller
#### Create a Job Template
Ensure that you have a Job Template ready to roll on Automation Controller - we'll be invoking this upon policy violations using the REST API. You'll have access to contextual information in an extra variable called `alert`, so make sure your Template indicates **Prompt on launch** for extra variables.

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

### Configure Event-Driven Ansible Controller
Next we'll fire up a Rulebook Activation on your Event-Driven Ansible controller to listen for incoming webhook requests.
* Be sure to use this repository for the Project (https://github.com/andykrohg/acs-ansible-notifier)
* Specify `acs-webhook.yml` as the Rulebook
* Use a default Decision Environment (`registry.redhat.io/ansible-automation-platform-24/de-supported-rhel8:latest`)
* Set a Variable for `eda_token`. We'll verify that incoming requests provide this token to protect access to the webhook. For example:
  ```yaml
  eda_token: abc123
  ```
* Save the Rulebook Activation and create a route to the generated Kubernetes Service. Then collect the route's hostname:
  ```bash
  oc expose $(oc get services -l app=eda -o name)
  oc get $(oc get route -o name -l app=eda) --template='{{ .spec.host }}'
  ```

### Create an Integration spec on ACS
* In ACS, go to **Platform Configuration -> Integrations**
* Select **Notifier Integrations -> Generic Webhook**, and click **New Integration**
* Give your integration a name that matches the Job Template you want to run (e.g. Revoke User Privileges), and provide the route to your Rulebook Activation in the **Endpoint**
* (Optional) Deselect **Enable audit logging** if all you want to do is respond to alerts
* Under **Headers**, add one that looks like this:
    * Key: `Authorization`
    * Value: `Bearer abc123` (the token value here should match the one you indicated in the Rulebook Activation)
* Under **Extra Fields**, add one that looks like this:
    * Key: `template_name`
    * Value: `<the name of your Ansible Job Template>`
* Then edit the Policy you want to notify on, and add this integration to the list of notifiers
