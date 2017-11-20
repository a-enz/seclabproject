# Create keystore for https and activate secure option in code
openssl pkcs12 -export -in ../Certificates/database.pem -inkey ../Certificates/database.key -out core_ca.p12 -name iMovies -passout pass:passwordThatShouldNotBeHardcoded
keytool -importkeystore -srckeystore core_ca.p12 -srcstoretype pkcs12 -destkeystore database.jks -deststoretype JKS -storepass passwordThatShouldNotBeHardcoded
keytool -import -trustcacerts -alias root -file ../Certificates/cacert.crt -keystore ca_cert.jks