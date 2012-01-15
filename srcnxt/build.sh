#!/bin/sh

nxjc *.java
nxjlink -o robustnxt.nxj RobustNXT
mkdir ../build
cp *.nxj ../build
