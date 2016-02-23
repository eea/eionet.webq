#!/bin/bash

# environment variables:
# BACKENDS - space separated list of backends
# BALANCER_AUDITORS - space separated list of allowed IP numbers (optional)
# SSL_CERT - ssl cert, a pem file with private key followed by public certificate, '\n'(two chars) as the line separator (optional)

CONFDIR=/usr/local/apache2/conf
#CONFDIR=.
CONFIGFILE=$CONFDIR/httpd.conf

#####################################################
function add_members {
    for backend in $BACKENDS
    do
       routenum=$(echo "$backend" | tr -d '.')
       echo "BalancerMember ajp://$backend:8009 route=$routenum" >> $CONFIGFILE
    done
}

#####################################################
function install_sslkey {
    echo "$SSL_KEY" > $CONFDIR/server.key
    echo "$SSL_CERT" > $CONFDIR/server.crt
    echo "$SSL_CHAIN_CERTS" > $CONFDIR/server-chain.crt
}

#####################################################
function create_core_conf {

if [ -n "$SERVER_NAME" ]; then
    echo "ServerName $SERVER_NAME" > $CONFIGFILE
fi
    cat >> $CONFIGFILE <<-!!
ServerRoot "/usr/local/apache2"
Listen 80
LoadModule authn_file_module modules/mod_authn_file.so
LoadModule authn_core_module modules/mod_authn_core.so
LoadModule authz_host_module modules/mod_authz_host.so
LoadModule authz_groupfile_module modules/mod_authz_groupfile.so
LoadModule authz_user_module modules/mod_authz_user.so
LoadModule authz_core_module modules/mod_authz_core.so
LoadModule access_compat_module modules/mod_access_compat.so
LoadModule auth_basic_module modules/mod_auth_basic.so
LoadModule socache_shmcb_module modules/mod_socache_shmcb.so
LoadModule socache_dbm_module modules/mod_socache_dbm.so
LoadModule socache_memcache_module modules/mod_socache_memcache.so
LoadModule reqtimeout_module modules/mod_reqtimeout.so
LoadModule filter_module modules/mod_filter.so
LoadModule mime_module modules/mod_mime.so
LoadModule log_config_module modules/mod_log_config.so
LoadModule env_module modules/mod_env.so
LoadModule headers_module modules/mod_headers.so
LoadModule setenvif_module modules/mod_setenvif.so
LoadModule version_module modules/mod_version.so
LoadModule unixd_module modules/mod_unixd.so
LoadModule status_module modules/mod_status.so
LoadModule autoindex_module modules/mod_autoindex.so
LoadModule dir_module modules/mod_dir.so
LoadModule alias_module modules/mod_alias.so
LoadModule ssl_module modules/mod_ssl.so
LoadModule remoteip_module modules/mod_remoteip.so
LoadModule proxy_module modules/mod_proxy.so
LoadModule proxy_connect_module modules/mod_proxy_connect.so
LoadModule proxy_ftp_module modules/mod_proxy_ftp.so
#LoadModule proxy_http_module modules/mod_proxy_http.so
LoadModule proxy_fcgi_module modules/mod_proxy_fcgi.so
LoadModule proxy_scgi_module modules/mod_proxy_scgi.so
#LoadModule proxy_wstunnel_module modules/mod_proxy_wstunnel.so
LoadModule proxy_ajp_module modules/mod_proxy_ajp.so
LoadModule proxy_balancer_module modules/mod_proxy_balancer.so
LoadModule proxy_express_module modules/mod_proxy_express.so
LoadModule session_module modules/mod_session.so
LoadModule session_cookie_module modules/mod_session_cookie.so
LoadModule session_crypto_module modules/mod_session_crypto.so
LoadModule session_dbd_module modules/mod_session_dbd.so
LoadModule slotmem_shm_module modules/mod_slotmem_shm.so
LoadModule lbmethod_byrequests_module modules/mod_lbmethod_byrequests.so
LoadModule lbmethod_bytraffic_module modules/mod_lbmethod_bytraffic.so
LoadModule lbmethod_bybusyness_module modules/mod_lbmethod_bybusyness.so
#LoadModule lbmethod_heartbeat_module modules/mod_lbmethod_heartbeat.so
<IfModule unixd_module>
User daemon
Group daemon
</IfModule>
ServerAdmin you@example.com
<Directory />
    AllowOverride none
    Require all denied
</Directory>
DocumentRoot "/usr/local/apache2/htdocs"
<Directory "/usr/local/apache2/htdocs">
    Options Indexes FollowSymLinks
    AllowOverride None
    Require all granted
</Directory>
<IfModule dir_module>
    DirectoryIndex index.html
</IfModule>
<Files ".ht*">
    Require all denied
</Files>
ErrorLog /proc/self/fd/2
LogLevel warn
<IfModule log_config_module>
    LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"" combined
    LogFormat "%h %l %u %t \"%r\" %>s %b" common
    <IfModule logio_module>
      LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\" %I %O" combinedio
    </IfModule>
    CustomLog /proc/self/fd/1 common
</IfModule>
<IfModule mime_module>
    TypesConfig conf/mime.types
</IfModule>
<IfModule proxy_html_module>
Include conf/extra/proxy-html.conf
</IfModule>
<IfModule ssl_module>
SSLRandomSeed startup builtin
SSLRandomSeed connect builtin
</IfModule>
!!

}
#####################################################
# SSL Loadbalancer
#####################################################
function create_https {

    cat >> $CONFIGFILE <<-!!

    Listen 443
    SSLCipherSuite HIGH:MEDIUM:!MD5:!RC4
    SSLProxyCipherSuite HIGH:MEDIUM:!MD5:!RC4
    SSLHonorCipherOrder on 
    SSLProtocol all -SSLv3
    SSLProxyProtocol all -SSLv3
    SSLPassPhraseDialog  builtin
    SSLSessionCache        "shmcb:/usr/local/apache2/logs/ssl_scache(512000)"
    SSLSessionCacheTimeout  300

    <VirtualHost _default_:443>
    ServerAdmin bdr.helpdesk@eea.europa.eu

    SSLEngine on
    SSLCertificateFile $CONFDIR/server.crt
    SSLCertificateKeyFile $CONFDIR/server.key
    SSLCertificateChainFile $CONFDIR/server-chain.crt

    Header add Set-Cookie "ROUTEID=.%{BALANCER_WORKER_ROUTE}e; path=/" env=BALANCER_ROUTE_CHANGED

    <Proxy balancer://mycluster>
!!
    add_members

    cat >> $CONFIGFILE <<!!

    ProxySet stickysession=ROUTEID failonstatus=500,503
    </Proxy>

    <Location /balancer-manager>
        SetHandler balancer-manager
        Order Deny,Allow
        Deny from all
        $ALLOW_FROM
    </Location>

    ProxyRequests Off
    ProxyTimeout 120
    ProxyPreserveHost On
    ProxyPass /balancer-manager !
    ProxyPass / balancer://mycluster/
    </VirtualHost>
!!

}

#####################################################
# Loadbalancer port 80
#####################################################
function create_lb {
cat >> $CONFIGFILE <<!!
ServerAdmin bdr.helpdesk@eea.europa.eu
Header add Set-Cookie "ROUTEID=.%{BALANCER_WORKER_ROUTE}e; path=/" env=BALANCER_ROUTE_CHANGED

<Proxy balancer://mycluster>
!!

add_members

cat >> $CONFIGFILE <<!!
ProxySet stickysession=ROUTEID failonstatus=500,503
</Proxy>

<Location /balancer-manager>
    SetHandler balancer-manager
    Order Deny,Allow
    Deny from all
    $ALLOW_FROM
</Location>

ProxyRequests Off
ProxyTimeout 120
ProxyPreserveHost On
ProxyPass /balancer-manager !
ProxyPass / balancer://mycluster/
!!
}

###########################################################
# MAIN
###########################################################
if [ -z "$BACKENDS" ]; then
    echo "The BACKENDS environment variable is not specified" 2>&1
    exit 2
fi
if [ -n "$BALANCER_AUDITORS" ]; then
    ALLOW_FROM="Allow from $BALANCER_AUDITORS"
fi

create_core_conf
create_lb

###########################################################
# SSL configuration
###########################################################
if [ -n "$SSL_CERT" -a -n "$SSL_KEY" ]; then
    create_https
    install_sslkey
fi
###########################################################
# Start HTTPD
###########################################################
exec /usr/local/bin/httpd-foreground
