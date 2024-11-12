# 도커가 없으면 설치
if ! type docker > /dev/null
then
  echo "docker does not exist"
  echo "Start installing docker"
  sudo apt-get update
  sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
  sudo apt update
  apt-cache policy docker-ce
  sudo apt install -y docker-ce
  sudo usermod -aG docker $USER
fi

# 도커 컴포즈가 없으면 설치
if ! type docker-compose > /dev/null
then
  echo "docker-compose does not exist"
  echo "Start installing docker-compose"
  sudo curl -L "https://github.com/docker/compose/releases/download/1.27.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
fi

# Docker 서비스 시작
sudo systemctl start docker
sudo systemctl enable docker

# MySQL 클라이언트 설치
sudo apt-get update
sudo apt-get install -y mysql-client

# 원격 MySQL 서버 연결 테스트
mysql -h "${MYSQL_HOST_IP}" -P 3306 -u timetrove -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" -e "SELECT 1;"

# 연결 성공 시 메시지 출력
if [ $? -eq 0 ]; then
    echo "Successfully connected to remote MySQL server"
else
    echo "Failed to connect to remote MySQL server"
    exit 1
fi