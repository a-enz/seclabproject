keytool -keystore cakeystore -genkey -noprompt \
	-alias caserver \
	-dname "CN=iMoviesCA, OU=IT, O=iMovies, C=CH" \
	-storepass password \
	-keypass password

#keytool -keystore cakeystore -certreq -alias caserver -keyalg rsa -file caserver.csr

# create csr

# TODO: sign csr with ca certificate
openssl ca -in caserver.csr -config ./ssl/CA/openssl.cnf

# The command imports the certificate and assumes the client certificate is in the file caserver.cer 
# and the CA’s certificate is in the file CARoot.cer.
#keytool -import -keystore cakeystore -file ca.cer -alias caserver

#keytool -import -keystore cakeystore -file ../Certificates/cacert.pem -alias cacert


#keytool -import -keystore cakeystore -file caserver.pem -alias caserver