# Iptables executed in privileged pod sample
In this sample, we respond to violations of the **Iptables or nftables Executed in Privileged Container** System Policy, which fires whenever someone executes a `iptables` or `nft` command in a privileged container.

In response, we spare no mercy and immediately revoke the associated user's OpenShift privileges until they can be disciplined, fired, or *other*. You can simulate this by running the following commands:
# OLD
```bash
oc run --rm -it --privileged --image soarinferret/iptablesproxy hack --command sh
iptables -h
```

# NEW
```bash
oc run --rm -it --privileged --overrides='{"apiVersion":"v1","spec":{"tolerations":[{"key":"demo","value":"sure","effect":"NoSchedule"}]}}' --image soarinferret/iptablesproxy hack --command sh
iptables -h
```
