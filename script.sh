#!/bin/bash

find . -name *.java -print0|xargs --null javac -d projet_lyz/Code/bin
cd projet_lyz/Code/bin
java Main
