projectDir=$1
repository=$2
projectType=$3
vcs=$4
module=$5
gitBranch=$6
env=$7
clientModule=$8


cd ${projectDir}
if [ $vcs -eq 1 ];then
    svn checkout ${repository}/${module}@HEAD  --username chutao --password ct2017
    svn checkout ${repository}/${clientModule}@HEAD  --username chutao --password ct2017
fi
if [ $vcs -eq 2 ];then
    git init
    git config core.sparseCheckout true
    echo /$module/* >> .git/info/sparse-checkout
    echo /$clientModule/* >> .git/info/sparse-checkout
    git remote add origin ${repository}
    git checkout -b ${gitBranch}
    git pull origin ${gitBranch%}
fi