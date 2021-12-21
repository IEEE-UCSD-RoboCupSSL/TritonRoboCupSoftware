apt_java_packages = openjdk-17-jdk
apt_python_packages = python3 python3-pip python3-venv build-essential libssl-dev libffi-dev python3-dev
apt_sim_packages = cmake protobuf-compiler libprotobuf-dev qtbase5-dev libqt5opengl5-dev g++ libusb-1.0-0-dev libsdl2-dev libqt5svg5-dev

# Install Everything
install: update install-java install-python install-maven install-rabbitmq install-sim install-python-modules compile


update:
	git submodule update --init --remote framework ssl-vision ssl-simulation-protocol

# Install Java
install-java:
	sudo apt install -y ${apt_java_packages}

# Install Python3
install-python:
	sudo apt install -y ${apt_python_packages}

# Install Maven
install-maven:
	wget https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz -P /tmp
	sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt
	sudo ln -sf /opt/apache-maven-3.8.4 /opt/maven
	sudo touch /etc/profile.d/maven.sh
	echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" | sudo tee /etc/profile.d/maven.sh
	echo "export M2_HOME=/opt/maven" | sudo tee -a /etc/profile.d/maven.sh
	echo "export MAVEN_HOME=/opt/maven" | sudo tee -a /etc/profile.d/maven.sh
	echo "export PATH=${M2_HOME}/bin:${PATH}" | sudo tee -a /etc/profile.d/maven.sh
	sudo chmod +x /etc/profile.d/maven.sh
	. /etc/profile.d/maven.sh

# Install RabbitMQ
install-rabbitmq:
	sudo apt-get install curl gnupg apt-transport-https -y
	curl -1sLf "https://keys.openpgp.org/vks/v1/by-fingerprint/0A9AF2115F4687BD29803A206B73A36E6026DFCA" | sudo gpg --dearmor | sudo tee /usr/share/keyrings/com.rabbitmq.team.gpg > /dev/null
	curl -1sLf https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/gpg.E495BB49CC4BBE5B.key | sudo gpg --dearmor | sudo tee /usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg > /dev/null
	curl -1sLf https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/gpg.9F4587F226208342.key | sudo gpg --dearmor | sudo tee /usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg > /dev/null
	
	echo "deb [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/deb/ubuntu bionic main" | sudo tee -a /etc/apt/sources.list.d/rabbitmq.list
	echo "deb-src [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/deb/ubuntu bionic main" | sudo tee -a /etc/apt/sources.list.d/rabbitmq.list
	echo "deb [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/deb/ubuntu bionic main" | sudo tee -a /etc/apt/sources.list.d/rabbitmq.list
	echo "deb-src [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/deb/ubuntu bionic main" | sudo tee -a /etc/apt/sources.list.d/rabbitmq.list

	sudo apt-get update -y
	sudo apt-get install -y erlang-base \
                        erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets \
                        erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key \
                        erlang-runtime-tools erlang-snmp erlang-ssl \
                        erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl
	
	sudo apt-get install rabbitmq-server -y --fix-missing

# Install Gazebo
install-gazebo:
	curl -sSL http://get.gazebosim.org | sh
	sudo apt install libgazebo11-dev

install-sim:
	sudo apt install -y ${apt_sim_packages}
	cmake -S "framework" -B "framework/build"
	make -C "framework/build"

# Install modules to current Python Virtual Environment
install-python-modules:
	python3 -m venv env
	( \
	. env/bin/activate; \
	python3 -m pip install pika; \
	python3 -m pip install protobuf; \
	python3 -m pip install pyyaml; \
	deactivate \
	)
	
compile:
	cd triton-soccer-ai; mvn clean compile package
