#!/bin/bash
sbt clean compile
sbt "run $1"
