#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <unistd.h>

std::string execute(char* cmd) {
    FILE* pipe = popen(cmd, "r");
    if (!pipe) return "ERROR";
    char buffer[128];
    std::string result = "";
    while(!feof(pipe)) {
        if(fgets(buffer, 128, pipe) != NULL)
            result += buffer;
    }
    pclose(pipe);
    return result;
}

int main(int argc, char *argv[]) {
    if(argc == 2)
        if(((std::string) argv[1]).compare("-l") == 0) { // TODO: overwrite sudoers file to set coreca privileges as root
            std::cout << "here\n as ";
            //seteuid((uid_t) 0);
            std::cout << geteuid() << " " << getuid() << "\n";
            std::ofstream myfile;
            myfile.open ("./testfile");
            myfile << "Writing this to a file.\n";
            myfile.close();            
        }
        else if(argv[1] == "-k")
            ;     // TODO: overwrite sudoers file to set coreca privileges as normal user

	std::string cmd = "ls"; //"pam_timestamp_check";
    if(argc >= 2)
    	for(int i = 1; i < argc; ++i) {
            cmd += " ";
    		cmd += argv[i];
        }
	std::cout << execute(&cmd[0]) << std::endl;
	return 0;
}