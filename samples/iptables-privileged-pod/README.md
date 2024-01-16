# Iptables executed in privileged pod sample
In this sample, we respond to violations of the **Iptables or nftables Executed in Privileged Container** System Policy, which fires whenever someone executes a `iptables` or `nft` command in a privileged container.

In response, we spare no mercy and immediately revoke the associated user's OpenShift privileges until they can be disciplined, fired, or *other*. You can simulate this by running the following commands:
```bash
oc run --rm -it --privileged --overrides='{"apiVersion":"v1","spec":{"tolerations":[{"key":"demo","value":"sure","effect":"NoSchedule"}]}}' --image soarinferret/iptablesproxy hack --command sh
iptables -h
```

## Preparing the MachineSet
Before executing the above commands though, we want to prepare the OpenShift environment. Specifically, this involves creating a MachineSet, which is responsible for creating the nodes whch will host the containers for this portion of the demo.

### 1. Configure the MachineSet Template:
Using the provided `machineset_template.yaml` file as a starting point, you'll need to fill in the specific values that match your OpenShift environment. 

**Tip:**  If you're unsure about what values to use, you can refer to a pre-existing MachineSet in your environment. This can provide a good baseline for the configurations needed.

### 2. Set Up Taints and Tolerations:
It's crucial to set up appropriate taints on your new nodes and corresponding tolerations on your containers. For this demo, ensure that the taint on the node and the toleration on the container match. In the `machineset_template.yaml` file, this is represented by a key-value pair under tolerations. For example:
```yaml
tolerations:
  - key: "demo"
    value: "sure"
    effect: "NoSchedule"
```
**Note:** This taint must match the toleration in your container specifications to ensure the container runs on the intended node.

### 3. Apply the MachineSet Configuration:
Once you have filled in the necessary details in the `machineset_template.yaml`, apply it to your OpenShift cluster using the following command:
```bash
oc apply -f machineset_template.yaml
```

### 4. Wait for Nodes to Spin Up:
After applying the MachineSet configuration, new nodes will start to spin up. This process may take a few minutes.

### 5. Verify Node Readiness:
Check the readiness of the new nodes:
```bash
oc get nodes
```

After setting up the MachineSet and ensuring the nodes are ready, proceed with the iptables command simulation in the privileged container as described above. 
