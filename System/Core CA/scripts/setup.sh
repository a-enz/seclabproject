# Set up CA stuff as showed at page 109 in the book
echo "Remove old files"
rm -r ./ssl/CA

echo "Create CA directories and files"
mkdir ./ssl/CA
mkdir ./ssl/CA/certs
mkdir ./ssl/CA/newcerts
mkdir ./ssl/CA/private
mkdir ./ssl/CA/crl
bash -c "echo '01' > ./ssl/CA/crlnumber"
mkdir ./tmp
bash -c "echo '01' > ./ssl/CA/serial"
touch ./ssl/CA/index.txt

echo "Create root key, self-signed root certificate and set them up"
openssl req -new -x509 -nodes -newkey rsa:2048 -extensions v3_ca -keyout cakey.pem -out cacert.pem -days 3650 -subj "/C=CH/ST=Zurich/L=Zurich/O=iMovies/OU=CA/CN=CA" -config ./ssl/openssl.cnf
mv cakey.pem ./ssl/CA/private/
mv cacert.pem ./ssl/CA/