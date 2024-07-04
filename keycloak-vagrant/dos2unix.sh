#!/bin/bash
dos2unix "/vagrant/dos2unix.sh"
dos2unix "/vagrant/environment.properties"
source /vagrant/environment.properties

echo -e "${BLUE}Dos2unix provisioning ${YELLOW}START${BLUE}!!!${NC}"

dos2unix_recursive() {
  local path=$1
  for file in "$path"/*; do
    if [[ -f "$file" ]]; then
      extension="${file##*.}"
      if [[ "$extension" == "sh" || "$extension" == "env" || "$extension" == "exp" || "$extension" == "py" ]]; then
        sudo dos2unix "$file"
        # sudo chmod 777 "$file"
      fi
    elif [[ -d "$file" ]]; then
      dos2unix_recursive "$file"
    fi
  done
}

dos2unix_recursive "/vagrant"

echo -e "${BLUE}Dos2unix provisioning ${GREEN}DONE${BLUE}!!!${NC}"
