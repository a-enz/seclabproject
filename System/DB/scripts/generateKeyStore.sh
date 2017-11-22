# Create keystore for https and activate secure option in code
openssl x509 -inform pem -in ../Certificates/database.pem -out ../Certificates/database.crt
openssl pkcs12 -export -in ../Certificates/database.pem -inkey ../Certificates/database.key -out database.p12 -name iMovies -passout pass:passwordThatShouldNotBeHardcoded
keytool -importkeystore -srckeystore database.p12 -srcstoretype pkcs12 -destkeystore database.jks -deststoretype JKS -storepass passwordThatShouldNotBeHardcoded
keytool -import -trustcacerts -alias root -file ../Certificates/cacert.crt -keystore database.jks