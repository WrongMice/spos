#include <bits/stdc++.h>
using namespace std;
/*
Completion Time -> Time required to complete the proccess
Waiting Time = Completion Time - Burst Time
Turn Around Time = Waiting Time + Burst Time
Service Time -> amount of time after which process start
*/
class Process
{
public:
int burstTime;
int arrivalTime;
int pid;
int priority;
int burstTimeRemaining;
int waitingTime;
int completionTime;
int turnaroundTime;
bool isComplete;
bool isInQueue;
Process()
{
burstTime = 0;
arrivalTime = 0;
pid = 0;
burstTimeRemaining = 0;
priority = 0;
waitingTime = 0;
turnaroundTime = 0;
completionTime = 0;
isComplete = false;
isInQueue = false;
}
};
void showResult(vector<Process> processes, vector<int> &waitingTime, vector<int> &turnAroundTime)
{
int n = processes.size();
int avgTurnAround = 0;
int avgWaitingTime = 0;
cout << "\tProcesses"
<< "\tBurst_Time"
<< "\tArrival_Time"
<< "\tWaiting_Time"
<< "\tTurnaround_Time"
<< "\n";
for (int i = 0; i < n; i++)
{
avgTurnAround += turnAroundTime[i];
avgWaitingTime += waitingTime[i];
cout << "\t\t" << processes[i].pid
<< "\t\t" << processes[i].burstTime
<< "\t\t" << processes[i].arrivalTime
<< "\t\t" << waitingTime[i]
<< "\t\t" << turnAroundTime[i]
<< "\n";
}
cout << "Average waiting time: " << (float)avgWaitingTime / (float)n << "\n";
cout << "Average turnaround time: " << (float)avgTurnAround / (float)n << "\n";
}
class FCFS
{
vector<int> findWaitingTime(vector<Process> processes)
{
int n = processes.size();
vector<int> serviceTime(n, 0);
vector<int> waitingTime(n, 0);
serviceTime[0] = processes[0].arrivalTime;
for (int i = 1; i < n; i++)
{
// calculating service time
serviceTime[i] = serviceTime[i - 1] + processes[i - 1].burstTime;
// calculating waiting time
waitingTime[i] = serviceTime[i] - processes[i].arrivalTime;
// if waiting time is negative means process already in queue
if (waitingTime[i] < 0)
{
waitingTime[i] = 0;
}
}
return waitingTime;
}
vector<int> findTurnAroundTime(vector<Process> processes, vector<int> &waitingTime)
{
int n = processes.size();
vector<int> turnAroundTime(n, 0);
for (int i = 0; i < n; i++)
{
// calculating turn around time
turnAroundTime[i] = waitingTime[i] + processes[i].burstTime;
}
return turnAroundTime;
}
public:
void fcfs(vector<Process> processes)
{
// find the waiting time
vector<int> waitingTime = findWaitingTime(processes);
// find the turnaround time
vector<int> turnAroundTime = findTurnAroundTime(processes, waitingTime);
// find the average
showResult(processes, waitingTime, turnAroundTime);
}
};
class SJF
{
vector<int> findWaitingTime(vector<Process> processes)
{
const int n = processes.size();
vector<int> waitingTime(n);
// step 1: calculate the remaining time
vector<int> remainingTime(n);
for (int i = 0; i < n; i++)
{
remainingTime[i] = processes[i].burstTime;
}
// step 2: Process the all the processes
int noOfCompleted = 0;
int time = 0;
int minimumTime = INT_MAX;
int shortestProcess = 0;
int finishTime = 0;
bool isFoundWithMinProcessTime = false;
while (noOfCompleted != n)
{
// find the process with minimum process time
for (int j = 0; j < n; j++)
{
// 1. If the arrival time is less than or equal to current time
// 2. Its remaining time is less than the minimum time
// 3. Remaining time is not 0 means it should not completed already
if (processes[j].arrivalTime <= time && remainingTime[j] < minimumTime && remainingTime[j] > 0)
{
minimumTime = remainingTime[j];
isFoundWithMinProcessTime = true;
shortestProcess = j;
}
}
// if process not found less than the current process then increment the time by 1 and continue
if (isFoundWithMinProcessTime == false)
{
time++;
continue;
}
// reduce the remaining time of shortest found process
remainingTime[shortestProcess]--;
// update the minium process time
minimumTime = remainingTime[shortestProcess];
// if minimum time is 0 means process is finished the set it to maximum value
if (minimumTime == 0)
{
minimumTime = INT_MAX;
}
// if the process completed
if (remainingTime[shortestProcess] == 0)
{
// increment the no of process completed
noOfCompleted++;
// reset the variables
isFoundWithMinProcessTime = false;
// finish time of current process
finishTime = time + 1;
// waiting time
waitingTime[shortestProcess] = finishTime - processes[shortestProcess].burstTime -
processes[shortestProcess].arrivalTime;
// if waiting time is negative means process is completed or in queue
if (waitingTime[shortestProcess] < 0)
{
waitingTime[shortestProcess] = 0;
}
}
// increment the time by 1
time++;
}
return waitingTime;
}
vector<int> findTurnAround(vector<Process> processes, vector<int> &waitingTime)
{
const int n = waitingTime.size();
vector<int> turnaround(n);
for (int i = 0; i < n; i++)
{
turnaround[i] = processes[i].burstTime + waitingTime[i];
}
return turnaround;
}
public:
void sjf(vector<Process> processes)
{
const int n = processes.size();
// waiting time
vector<int> waitingTime = findWaitingTime(processes);
// turn around time
vector<int> turnAroundTime = findTurnAround(processes, waitingTime);
// find the average ans show the result
showResult(processes, waitingTime, turnAroundTime);
}
};
class Priority
{
static bool comp(Process first, Process second)
{
if (first.arrivalTime == second.arrivalTime)
return first.priority < second.priority;
return first.arrivalTime < second.arrivalTime;
}
vector<int> findWaitingTime(vector<Process> &processes)
{
int n = processes.size();
vector<int> serviceTime(n, 0);
vector<int> waitingTime(n, 0);
serviceTime[0] = processes[0].arrivalTime;
for (int i = 1; i < n; i++)
{
// calculating service time
serviceTime[i] = serviceTime[i - 1] + processes[i - 1].burstTime;
// calculating waiting time
waitingTime[i] = serviceTime[i] - processes[i].arrivalTime;
// if waiting time is negative means process already in queue
if (waitingTime[i] < 0)
{
waitingTime[i] = 0;
}
}
return waitingTime;
}
vector<int> findTurnAroundTime(vector<Process> &processes, vector<int> &waitingTime)
{
int n = processes.size();
vector<int> turnAroundTime(n, 0);
for (int i = 0; i < n; i++)
{
// calculating turn around time
turnAroundTime[i] = waitingTime[i] + processes[i].burstTime;
}
return turnAroundTime;
}
public:
void priority(vector<Process> processes)
{
int n = processes.size();
// sort by arrival time or priority
sort(processes.begin(), processes.end(), comp);
// find waiting time
vector<int> waitingTime = findWaitingTime(processes);
// find turn around time
vector<int> turnaroundTime = findTurnAroundTime(processes, waitingTime);
// show result
// showResult(processes, waitingTime, turnaroundTime);
int avgTurnAround = 0;
int avgWaitingTime = 0;
cout << "\tProcesses"
<< "\tBurst_Time"
<< "\tArrival_Time"
<< "\tPriority"
<< "\tWaiting_Time"
<< "\tTurnaround_Time"
<< "\tCompletion_Time"
<< "\n";
for (int i = 0; i < n; i++)
{
avgTurnAround += turnaroundTime[i];
avgWaitingTime += waitingTime[i];
cout << "\t\t" << processes[i].pid
<< "\t\t" << processes[i].burstTime
<< "\t\t" << processes[i].arrivalTime
<< "\t\t" << processes[i].priority
<< "\t\t" << waitingTime[i]
<< "\t\t" << turnaroundTime[i]
<< "\n";
}
cout << "Average waiting time: " << (float)avgWaitingTime / (float)n << "\n";
cout << "Average turnaround time: " << (float)avgTurnAround / (float)n << "\n";
}
};
class RoundRobin
{
static bool comp(Process &first, Process &second)
{
return first.arrivalTime < second.arrivalTime;
}
void checkForNewArrival(vector<Process> &processes, queue<Process> &readyQueue, int &currentTime)
{
int n = processes.size();
for (int i = 0; i < n; i++)
{
Process current = processes[i];
// check the arrival time is less than the current time and it is not in queue or complete
if (current.arrivalTime <= currentTime && current.isInQueue == false && current.isComplete == false)
{
processes[i].isInQueue = true;
cout << current.pid << endl;
readyQueue.push(processes[i]);
}
}
}
void processQueue(vector<Process> &processes, int quantum, queue<Process> &readyQueue, int &currentTime, int
&numberOfProgramExecuted)
{
Process current = readyQueue.front();
readyQueue.pop();
int n = processes.size();
// if process burst time is less thant the time quantum
if (current.burstTimeRemaining <= quantum && current.isComplete == false)
{
currentTime += current.burstTimeRemaining;
current.isComplete = true;
current.completionTime = currentTime;
current.waitingTime = current.completionTime - current.arrivalTime - current.burstTime;
current.turnaroundTime = current.waitingTime + current.burstTime;
current.burstTimeRemaining = 0;
if (current.waitingTime < 0)
{
current.waitingTime = 0;
}
processes[current.pid - 1] = current;
// if all processes not in queue then take them in queue
if (numberOfProgramExecuted != n)
{
checkForNewArrival(processes, readyQueue, currentTime);
}
}
else
{
// the process is not complete yet
current.burstTimeRemaining -= quantum;
currentTime += quantum;
// if all processes not in queue then take them in queue
if (numberOfProgramExecuted != n)
{
checkForNewArrival(processes, readyQueue, currentTime);
}
current.isInQueue = true;
processes[current.pid - 1] = current;
readyQueue.push(current);
cout << current.pid << endl;
}
}
public:
void roundRobin(vector<Process> &processes, int &quantum)
{
// sort the process by arrival time
sort(processes.begin(), processes.end(), comp);
// process queue
queue<Process> readyQueue;
// initial push the first process arrives first
cout << processes[0].pid << endl;
readyQueue.push(processes[0]);
processes[0].isInQueue = true;
// current time of the processes executed
int currentTime = 0;
// number of processes executed
int numberOfProgramExecuted = 0;
while (!readyQueue.empty())
{
processQueue(processes, quantum, readyQueue, currentTime, numberOfProgramExecuted);
}
double waitingTime = 0;
double turnaroundTime = 0;
int n = processes.size();
cout << "\tProcesses"
<< "\tBurst_Time"
<< "\tArrival_Time"
<< "\tWaiting_Time"
<< "\tTurnaround_Time"
<< "\n";
for (int i = 0; i < n; i++)
{
turnaroundTime += processes[i].turnaroundTime;
waitingTime += processes[i].waitingTime;
cout << "\t\t" << processes[i].pid
<< "\t\t" << processes[i].burstTime
<< "\t\t" << processes[i].arrivalTime
<< "\t\t" << processes[i].waitingTime
<< "\t\t" << processes[i].turnaroundTime
<< "\n";
}
cout << "Average waiting time: " << (float)waitingTime / (float)n << "\n";
cout << "Average turnaround time: " << (float)turnaroundTime / (float)n << "\n";
}
};
class Scheduling : FCFS, SJF, Priority, RoundRobin
{
public:
Scheduling() {}
void fcfs(vector<Process> &processes)
{
FCFS::fcfs(processes);
}
void sjf(vector<Process> &processes)
{
SJF::sjf(processes);
}
void priority(vector<Process> &processes)
{
Priority::priority(processes);
}
void roundRobin(vector<Process> &processes, int quantum)
{
RoundRobin::roundRobin(processes, quantum);
}
};
int menu()
{
cout << "------------ Scheduling Algorithms --------------- \n";
cout << "\t1. FCFS\n";
cout << "\t2. SJF\n";
cout << "\t3. Priority\n";
cout << "\t4. Round Robin\n";
cout << "\tEnter your choice:";
int choice;
cin >> choice;
return choice;
}
vector<Process> takeInput(int n)
{
vector<Process> processes(n);
cout << "\nEnter arrival time and burst time: \n";
for (int i = 0; i < n; i++)
{
cin >> processes[i].arrivalTime >> processes[i].burstTime;
processes[i].pid = i + 1;
processes[i].burstTimeRemaining = processes[i].burstTime;
}
return processes;
}
int main()
{
int n;
cout << "Number of processes: ";
cin >> n;
vector<Process> processes = takeInput(n);
Scheduling schedule;
bool run = true;
while (run)
{
switch (menu())
{
case 1:
schedule.fcfs(processes);
break;
case 2:
schedule.sjf(processes);
break;
case 3:
cout << "Enter priorites of the processes : \n";
for (int i = 0; i < n; i++)
{
cout << "Process " << processes[i].pid << ": ";
cin >> processes[i].priority;
}
schedule.priority(processes);
break;
case 4:
cout << "Enter quantum : ";
int quantum;
cin >> quantum;
schedule.roundRobin(processes, quantum);
break;
default:
cout << "Option does not exits!!!! \n";
run = false;
break;
}
}
return 0;
}