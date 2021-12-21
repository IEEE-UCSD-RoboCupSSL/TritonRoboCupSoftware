import subprocess 
import os
import time

dir_path = os.path.dirname(os.path.realpath(__file__))
triton_soccer_ai_jar_path = dir_path + "/triton-soccer-ai/target/"
triton_soccer_ai_py_path = dir_path + "/triton-soccer-ai/src/main/python"
triton_bot_path = dir_path + "/triton-bot/"
sim_path = dir_path + "/framework/build/bin"

def run_cmd(cmd, path, mode="current"):
    # annoying lengthy path is removed from the print out
    prt_cmd_str = (' '.join(map(str, cmd))).replace(triton_bot_path,"")
    if (mode == "current"):
        print(">>> running " + prt_cmd_str + " in current terminal")
        subprocess.Popen(cmd, cwd=path)
    if (mode == "background"):
        print(">>> running " + prt_cmd_str + " as background process")
        subprocess.Popen(cmd, cwd=path, stdout=subprocess.PIPE) 
    if (mode == "tab"):
        print(">>> running " + prt_cmd_str + " in a new terminal tab")
        subprocess.Popen(["gnome-terminal", "--tab", "--"] + cmd, cwd=path)

run_cmd(["./simulator-cli", "-g", "2020B", "--realism", "Realistic"], sim_path, "tab")
time.sleep(1)

triton_bot_py = [
    "triton_bot.py"
]
for py in triton_bot_py:
    run_cmd(["python", py], triton_bot_path, "tab")
    time.sleep(1)

run_cmd(["java", "-jar", "TritonSoccerAI.jar", "yellow"], triton_soccer_ai_jar_path, "tab")
time.sleep(1)

triton_soccer_ai_py_modules = [
    # "AI_C.py"
]
for py in triton_soccer_ai_py_modules:
    run_cmd(["python", py], triton_soccer_ai_py_path, "tab")
    time.sleep(1)