# ACS Ansible Notifier
This app is intended to serve as an intermediary between a Stackrox Generic Webhook notifier in Advanced Cluster Security and an instance of Ansible Automation Controller. It's useful in the event that you wish to run an Ansible job in response to an alert from ACS, such as a policy violation.

## Create a Job Template on Ansible Automation Controller
Ensure that you have a Job Template ready to roll on Automation Controller - we'll be invoking this upon policy violations using the REST API. You'll have access to contextual information in an extra variable called `alert`, so make sure your Template indicates **Prompt on launch** for extra variables. There's an example playbook in the `ansible` directory of this repository. Make note of this templates *numerical template id*, viewable in the address bar.

## Run this on OpenShift
```bash
# Set environment variables
ANSIBLE_API_SERVER_URL=https://automation-controller.com/api
ANSIBLE_USERNAME=admin
ANSIBLE_PASSWORD=password

oc new-app quay.io/akrohg/acs-ansible-notifier \
    -e ANSIBLE_API_SERVER_URL=$ANSIBLE_API_SERVER_URL \
    -e ANSIBLE_USERNAME=$ANSIBLE_USERNAME \
    -e ANSIBLE_PASSWORD=$ANSIBLE_PASSWORD

oc expose svc/acs-ansible-notifier
```

## Create an Integration spec on ACS
* Select **Generic Webhook**
* Provide the route to your `acs-ansible-notifier` app in the **Endpoint**
* Under **Extra Fields**, add one like this:
    * Key: `template_id`
    * Value: `<your numerical ansible job template id>`
* Then edit the Policy you want to notify on, and add this integration to the list of notifiers