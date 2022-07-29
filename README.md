# ACS Ansible Notifier
This app is intended to serve as an intermediary between a Stackrox Generic Webhook notifier in Advanced Cluster Security and an instance of Ansible Automation Controller. It's useful in the event that you wish to run an Ansible job in response to an alert from ACS, such as a policy violation.

## Create a Job Template on Ansible Automation Controller
Ensure that you have a Job Template ready to roll on Automation Controller - we'll be invoking this upon policy violations using the REST API. You'll have access to contextual information in an extra variable called `alert`, so make sure your Template indicates **Prompt on launch** for extra variables. There's an example playbook in the `ansible` directory of this repository. Make note of this templates *numerical template id*, viewable in the address bar.

## Deploy the Notifier
Next, we'll deploy an instance of this project to OpenShift.
```bash
# Set environment variable for server URL. Be sure to include /api
ANSIBLE_API_SERVER_URL=https://automation-controller.com/api

oc new-app quay.io/akrohg/acs-ansible-notifier \
    -e ANSIBLE_API_SERVER_URL=$ANSIBLE_API_SERVER_URL

oc expose svc/acs-ansible-notifier
```

## Create an Integration spec on ACS
* In ACS, go to **Platform Configuration -> Integrations**
* Select **Notifier Integrations -> Generic Webhook**, and click **New Integration**
* Give your integration a name, and provide the route to your `acs-ansible-notifier` app in the **Endpoint**
* (Optional) Deselect **Skip TLS verification** if your Automation Controller isn't using a well-known certificate
* (Optional) Deselect **Enable audit logging** if all you want to do is respond to alerts
* Provide a **Username** and **Password** to authenticate to Automation Controller
* Under **Extra Fields**, add one that looks like this:
    * Key: `template_id`
    * Value: `<your numerical ansible job template id>`
* Then edit the Policy you want to notify on, and add this integration to the list of notifiers