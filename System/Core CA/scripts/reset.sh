# Set up CA stuff as showed at page 109 in the book
echo "Remove old files"
rm -r ./ssl

echo "Create copy from backup"
cp -r ./ssl_base/ ./ssl