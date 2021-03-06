# Create keystore for https and activate secure option in code
openssl x509 -inform pem -in ../Certificates/core_ca.pem -out ../Certificates/core_ca.crt
openssl pkcs12 -export -in ../Certificates/core_ca.pem -inkey ../Certificates/core_ca.key -out core_ca.p12 -name iMovies -passout pass:passwordThatShouldNotBeHardcoded
keytool -importkeystore -srckeystore core_ca.p12 -srcstoretype pkcs12 -destkeystore core_ca.jks -deststoretype JKS -storepass passwordThatShouldNotBeHardcoded
keytool -import -trustcacerts -alias root -file ../Certificates/cacert.crt -keystore core_ca.jks