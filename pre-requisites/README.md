# ACS Ansible Notifier Demo Pre-requisites
This repo houses example playbooks to facilitate integration use cases between Advanced Cluster Security and an instance of Ansible Automation Controller using Event-Driven Ansible. It's useful in the event that you wish to run an Ansible job in response to an alert from ACS, such as a policy violation.

## Pre-requisites Deployment Guide
### Configure Ansible Automation Controller
#### Create a Job Template
Ensure that you have a Job Template ready to roll on Automation Controller - we'll be invoking this upon policy violations using the REST API. You'll have access to contextual information in an extra variable called `alert`, so make sure your Template indicates **Prompt on launch** for extra variables.
