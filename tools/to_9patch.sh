#!/bin/bash
for i in $(ls *.png | grep "-"); do
	NEW_NAME=`echo "$i" | sed -e "s/-/_/g"`
	mv "$i" "$NEW_NAME"
done
