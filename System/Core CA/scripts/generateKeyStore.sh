keytool -keystore cakeystore -genkey -noprompt \
	-alias ca \
	-dname "CN=iMoviesCA, OU=IT, O=iMovies, C=CH" \
	-storepass password \
	-keypass password

keytool -keystore cakeystore -certreq -alias ca -keyalg rsa -file ca.csr

# TODO: sign csr with ca certificate


# The command imports the certificate and assumes the client certificate is in the file client.cer 
# and the CA’s certificate is in the file CARoot.cer.
#keytool -import -keystore clientkeystore -file client.cer -alias client