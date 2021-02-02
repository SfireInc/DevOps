FROM httpd:2.4.46

COPY ./httpd/html/ /usr/local/apache2/htdocs/
COPY ./httpd/httpd.conf /usr/local/apache2/conf/httpd.conf