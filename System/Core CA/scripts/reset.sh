# Set up CA stuff as showed at page 109 in the book
echo "Remove old files"
mv ./ssl/CA/cacert.pem ./ssl
mv ./ssl/CA/private/cakey.pem ./ssl
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

echo "Place root key and self-signed root certificate at the right place"
mv cakey.pem ./ssl/CA/private/
mv cacert.pem ./ssl/CA/