# See https://raymii.org/s/tutorials Encrypt_and_decrypt_files_to_public_keys_via_the_OpenSSL_Command_Line.html

# Generate random 256 key for symmestric encryption
openssl rand -base64 32 > symm.key

# Encrypt it with backup public key
openssl rsautl -encrypt -inkey ../Certificates/backup.pub -pubin -in symm.key -out symm.enc

# Encrypt file with symmetric key


# Decrypt symmetric key
openssl rsautl -decrypt -inkey privatekey.pem -in key.bin.enc -out key.bin

# Decrypt private key
openssl enc -d -aes-256-cbc -in largefile.pdf.enc -out largefile.pdf -pass file:./bin.key