# Create ssh directory where to store known hosts file
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# Add fingerprint og gitlab.vis.ethz.ch to known hosts
echo "Get gitlab.vis.ethz.ch fingerprint"
ssh-keyscan -H gitlab.vis.ethz.ch >> ~/.ssh/known_hosts

# Sparse clone needed files from git repository
# for more info see https://stackoverflow.com/questions/600079/how-do-i-clone-a-subdirectory-only-of-a-git-repository/13738951#13738951
# TODO: make more efficient by retireving only files and not also .git directory
echo "Sparse clone $1 directory"
mkdir -p ~/SecLabProject
cd ~/SecLabProject
git init
git remote add -f origin git@gitlab.vis.ethz.ch:abaehler/SecLabProject.git
git config core.sparseCheckout true
echo "$1" >> .git/info/sparse-checkout
git pull --depth=1 origin master