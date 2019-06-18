from locust import HttpLocust, TaskSet, task
import json
import random

class UserBehavior(TaskSet):

    id = 0 # transaction id of last record in db

    def on_start(self):
        """ on_start is called when a Locust start before any task is scheduled """
        self.log_in()

    def on_stop(self):
        """ on_stop is called when the TaskSet is stopping """
        self.log_out()

    def log_in(self):
        print("login")

    def log_out(self):
        print("logout")

    @task(1)
    def index(self):
        amount = random.randint(1000,50001) # between 1000 and 50000
        accountId = random.randint(1,3) # between 1 and 2
        UserBehavior.id = UserBehavior.id + 1 # transaction id increment
        headers = {'content-type': 'application/json'} # headers to post request as json
        payload = {"id":str(UserBehavior.id), "accountId": str(accountId),"amount": str(amount)} # transaction json format
        #print(UserBehavior.id)
        #print(payload)
        self.client.post("/make-transaction", data=json.dumps(payload), headers=headers) # post request

    # @task(1)
    # def index(self):
    #     self.client.get("/")

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 5000
    max_wait = 9000
    host = 'http://localhost:8764'