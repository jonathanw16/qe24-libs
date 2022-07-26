import base64
import copy
import json
import socket
import string
import sys
import threading
from concurrent.futures import as_completed
from concurrent.futures.thread import ThreadPoolExecutor
from datetime import datetime
from http.client import RemoteDisconnected, IncompleteRead

from couchbase.cluster import Cluster, ClusterOptions, QueryOptions, ClusterTimeoutOptions
from couchbase.exceptions import QueryException, QueryIndexAlreadyExistsException, TimeoutException
from couchbase_core.cluster import PasswordAuthenticator
from couchbase.management.collections import *
from couchbase.management.admin import *
import random
import argparse
import logging
import requests
import time
import httplib2
import json
import paramiko
import dns.resolver

class updateAMI:

    def __init__(self):
        parser = argparse.ArgumentParser()
        parser.add_argument("-u", "--username", help="Couchbase Server Cluster Username")
        parser.add_argument("-p", "--password", help="Couchbase Server Cluster Password")
        parser.add_argument("-c", "--connectstring", help="Couchbase Server Connection String")
        parser.add_argument("-m", "--name", help="Name of AMI")
        parser.add_argument("-k", "--key", help="Key to be added")
        parser.add_argument("-v", "--value", help="Value to be added")
        parser.add_argument("-a", "--action",
                            choices=["checkname", "update"],
                            help="Choose an action to be performed. Valid actions : checkname, update",
                            default="checkname")

    args = parser.parse_args()
    self.username = args.username
    self.password = args.password
    self.action = args.action
    self.connectstring = args.connectstring
    self.name = args.name
    self.key = args.key
    self.value = args.value

    
    timeout_options = ClusterTimeoutOptions(kv_timeout=timedelta(seconds=120), query_timeout=timedelta(seconds=10))
    options = ClusterOptions(PasswordAuthenticator(self.username, self.password), timeout_options=timeout_options)
    self.cluster = Cluster(self.connectstring, options)
    self.cb = self.cluster.bucket("qe24-status")
    self.coll = self.cb.scope("_default").collection("_default")

    def checkname(self):
        try:
            result = self.coll.get(self.name)
            return True
        except Exception as e:
            document = {"AMI" : self.name, "PIPELINE_STATUS" : "STARTED", "dev-pipeline" : {:}, "stage-pipeline", {:}}
            result = self.coll.insert(self.name, document)
            return False
    
    def updateDoc(self):
        try:
            self.coll.mutate_in(self.name, SD.upsert(self.key, self.value))
            return "Success"
        except Exception as e:
            return "Failed"

if __name__ == '__main__':
    update_ami = updateAMI()

    if update_ami.action == "checkname":
        update_ami.checkname()
    elif update_ami.action == "update":
        update_ami.updateDoc()
    else:
        print("invalid action")
