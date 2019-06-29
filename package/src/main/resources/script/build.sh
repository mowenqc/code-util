projectDir=$1
repository=$2
projectType=$3
vcs=$4
module=$5
gitBranch=$6
env=$7

cd ${projectDir}
cd ${module}
if [ $projectType -eq 1 ];then
    mvn clean package -Denv=${env} -U
fi
if [ $projectType -eq 2 ];then
    mvn clean deploy -U
fi