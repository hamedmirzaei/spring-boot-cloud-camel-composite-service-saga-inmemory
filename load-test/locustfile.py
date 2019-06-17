from locust import HttpLocust, TaskSet, task
import json

class UserBehavior(TaskSet):
    def on_start(self):
        """ on_start is called when a Locust start before any task is scheduled """
        self.login()

    def on_stop(self):
        """ on_stop is called when the TaskSet is stopping """
        self.logout()

    def login(self):
        print("login")
        #self.client.post("/login", {"username":"ellen_key", "password":"education"})

    def logout(self):
        print("logout")
        #self.client.post("/logout", {"username":"ellen_key", "password":"education"})

    @task(1)
    def index(self):
        headers = {'content-type': 'application/json'}
        payload = {"id":"5", "accountId": "1","amount": "2000"}

        self.client.post("/make-transaction", data=json.dumps(payload), headers=headers)

    # @task(1)
    # def index(self):
    #     self.client.get("/")

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 5000
    max_wait = 9000
    host = 'http://localhost:8764'