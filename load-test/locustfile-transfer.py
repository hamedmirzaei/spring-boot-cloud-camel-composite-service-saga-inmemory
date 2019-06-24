from locust import HttpLocust, TaskSet, task
import json
import random
from locust.exception import StopLocust
from locust.main import runners

class UserBehavior(TaskSet):

    id = 0 # transaction id of last record in db

    def on_start(self):
        """ on_start is called when a Locust start before any task is scheduled """
        # self.log_in()
        pass

    def on_stop(self):
        """ on_stop is called when the TaskSet is stopping """
        # self.log_out()
        pass

    def log_in(self):
        print("login")

    def log_out(self):
        print("logout")

    @task(1)
    def index(self):
        amount = random.randint(1000,50001) # between 1000 and 50000
        account_Id = random.randint(1,101) # between 1 and 100
        UserBehavior.id = UserBehavior.id + 1 # transaction id increment
        headers = {'content-type': 'application/json'} # headers to post request as json
        payload = {"id":str(UserBehavior.id), "accountId": str(account_Id),"amount": str(amount)} # transaction json format
        #print(UserBehavior.id)
        #print(payload)
        if UserBehavior.id == 200:
            raise StopLocust()
            runners.locust_runner.quit()
        else:
            self.client.post("/transfer", data=json.dumps(payload), headers=headers) # post request

    # @task(1)
    # def index(self):
    #     self.client.get("/")

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 5000
    max_wait = 9000
    host = 'http://localhost:8767'