FROM diouxx/apache-proxy:latest

# from https://www.digitalocean.com/community/tutorials/how-to-use-apache-http-server-as-reverse-proxy-using-mod_proxy-extension


RUN a2enmod headers
RUN a2enmod proxy
RUN a2enmod proxy_http
RUN a2enmod proxy_balancer
RUN a2enmod proxy_connect
RUN a2enmod proxy_html
RUN a2enmod lbmethod_byrequests
RUN a2enmod lbmethod_bytraffic
RUN a2enmod lbmethod_bybusyness
RUN a2enmod lbmethod_heartbeat


ADD ./conf/proxy.conf /opt/proxy-conf/

### from https://github.com/tutumcloud/dnsutils/blob/master/Dockerfile

RUN apt-get update && apt-get install -yq dnsutils && apt-get clean && rm -rf /var/lib/apt/lists

### from https://serverfault.com/a/227194

### ENV DEBIAN_FRONTEND=noninteractive

### from https://blog.hostonnet.com/installing-nslookup-in-ubuntu

### RUN apt-get -yq install dnsutils

### from https://github.com/powerman/dockerize

### debian, ubuntu:
RUN apt update && apt install -y curl

RUN curl -sfL https://github.com/powerman/dockerize/releases/download/v0.10.0/dockerize-`uname -s`-`uname -m` | install /dev/stdin /usr/local/bin/dockerize


ENTRYPOINT ["dockerize", "--stdout", "/var/log/apache2/foobar.log", "--stdout", "/var/log/apache2/acces.log", "--stdout", "/var/log/apache2/error.log", "/opt/apache-proxy-start.sh"]