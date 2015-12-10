git pull
mvn clean install -DskipTests
ps aux | grep -ie direct_democracy | awk '{print $2}' | xargs kill -9
sleep 2
nohup ./run.sh $@ &>log.out &
tail -f log.out
