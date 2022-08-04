import base64
import copy
import socket
import string
import sys
import threading
from datetime import datetime
from http.client import RemoteDisconnected, IncompleteRead
from datetime import timedelta

from couchbase.auth import PasswordAuthenticator
from couchbase.options import ClusterOptions, ClusterTimeoutOptions
from couchbase.cluster import Cluster, QueryOptions
import couchbase.subdocument as SD
from couchbase.exceptions import QueryIndexAlreadyExistsException, TimeoutException
from couchbase.durability import (Durability, ServerDurability,
                                  ClientDurability, ReplicateTo, PersistTo)
from couchbase.collection import (
    InsertOptions,
    ReplaceOptions,
    UpsertOptions,
    GetOptions,
    RemoveOptions,
    IncrementOptions,
    DecrementOptions)

import random
import argparse
import logging
import time
import json


class updateAMI:

    def __init__(self):
        parser = argparse.ArgumentParser()
        parser.add_argument("-u", "--username", help="Couchbase Server Cluster Username")
        parser.add_argument("-p", "--password", help="Couchbase Server Cluster Password")
        parser.add_argument("-c", "--connectstr", help="Couchbase Server Connection String")
        parser.add_argument("-m", "--name", help="Name of AMI")
        parser.add_argument("-k", "--key", help="Key to be added")
        parser.add_argument("-v", "--value", help="Value to be added")
        parser.add_argument("-e", "--env", help="Pipeline Environment")
        parser.add_argument("-a", "--action",
                            choices=["checkname", "update", "latest"],
                            help="Choose an action to be performed. Valid actions : checkname, update",
                            default="checkname")

        args = parser.parse_args()
        self.username = args.username
        self.password = args.password
        self.action = args.action
        self.connectstring = args.connectstr
        self.name = args.name
        self.key = args.key
        self.value = args.value
        self.env = args.env
        
        timeout_opts = ClusterTimeoutOptions(kv_timeout=timedelta(seconds=120), query_timeout=timedelta(seconds=10), connect_timeout=timedelta(seconds=30))
        auth = PasswordAuthenticator(self.username, self.password)
        self.cluster = Cluster(self.connectstring, ClusterOptions(auth, timeout_options=timeout_opts))
        self.cb = self.cluster.bucket("qe24_status")
        self.coll = self.cb.scope("_default").collection("_default")

    def checkname(self):
        try:
            result = self.coll.get(self.name)
            print("Found")
            return True
        except Exception as e:
            document = {"AMI" : self.name, "dev" : {"PIPELINE_STATUS" : "STARTED"}, "stage" : {"PIPELINE_STATUS" : "NOT STARTED"}}
            result = self.coll.insert(self.name, document)
            print("Doc Created")
            return False
    
    def updateDoc(self):
        try:
            self.coll.mutate_in(self.name, [SD.upsert(self.key, self.value)])
            return "Success"
        except Exception as e:
            return "Failed"

    def getLatest(self):
        try:
            result = self.cluster.query(
                "select AMI from `qe24_status` where {}.latest=$1".format(self.env),
                True
            )
            for entry in result:
                print(entry["AMI"])
            return True
        except Exception:
            print("wWAWWAW I FAILED MANNNN")
            return False

if __name__ == '__main__':
    update_ami = updateAMI()

    if update_ami.action == "checkname":
        update_ami.checkname()
    elif update_ami.action == "update":
        update_ami.updateDoc()
    elif update_ami.action == "latest":
        update_ami.getLatest()
    else:
        print("invalid action")