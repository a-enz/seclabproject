from django.db import models

# Create your models here.
class UserInfo(models.Model):
    uid = models.CharField(max_length=10)
    lastname = models.CharField(max_length=50)

    def __str__(self):
        return self.lastname
