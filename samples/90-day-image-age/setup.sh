#!/bin/sh
# Deploy standalone docker registry
oc new-app registry -e REGISTRY_STORAGE_DELETE_ENABLED=true
oc create route edge registry --service=registry

# Copy image from quay.io into registry
REGISTRY_HOST=$(oc get route registry --template='{{ .spec.host }}')
skopeo copy docker://quay.io/aromero/dc-metro-map:latest docker://$REGISTRY_HOST/aromero/dc-metro-map:latest

# Create a new app referencing the temporary image
oc new-app $REGISTRY_HOST/aromero/dc-metro-map:latest
oc patch deployment dc-metro-map -p '{"spec":{"template":{"spec":{"containers":[{"name":"dc-metro-map","imagePullPolicy":"Always"}]}}}}'
oc expose service/dc-metro-map