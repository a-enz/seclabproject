from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^user_login/$', views.user_login, name='user_login'),
    url(r'^welcome/$', views.welcome, name='welcome'),
    url(r'^display_user_info/$', views.display_user_info, name='display_user_info'),
    url(r'^display_admin_info/$', views.display_admin_info, name='display_admin_info'),
    url(r'^goodbye/$', views.all_logout, name='goodbye'),
    url(r'^new/$', views.new, name='new'),
    url(r'^crl/$', views.crl, name='crl'),
    url(r'^revoke_all/$', views.revoke_all, name='revoke_all'),
    url(r'^revoke_single/$', views.revoke_single, name='revoke_single'),
]
