FROM websphere-liberty:latest

MAINTAINER Erin Schnabel <schnabel@us.ibm.com> (@ebullientworks)

ADD https://download.elastic.co/logstash-forwarder/binaries/logstash-forwarder_linux_amd64 /opt/forwarder
ADD ./servers/board /opt/ibm/wlp/usr/servers/defaultServer/

#RUN apt-get update; apt-get install -y curl
RUN /opt/ibm/wlp/bin/installUtility install  --acceptLicense /opt/ibm/wlp/usr/servers/defaultServer/server.xml

RUN wget https://github.com/coreos/etcd/releases/download/v2.2.2/etcd-v2.2.2-linux-amd64.tar.gz -q ; \
    tar xzf etcd-v2.2.2-linux-amd64.tar.gz etcd-v2.2.2-linux-amd64/etcdctl --strip-components=1 ; \
    rm etcd-v2.2.2-linux-amd64.tar.gz ; \
    mv etcdctl /usr/local/bin/etcdctl

COPY ./startup.sh /opt/startup.sh

CMD ["/opt/startup.sh"]
