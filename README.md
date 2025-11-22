# LANChat 

## Basic Details

**Hosted Project Link:** `[Add your GitHub link here]`

## Project Description

**LANChat** is an offline LAN-based chat application built in Java. It allows multiple users to communicate over a local network without the need for internet. Features include:

* Direct/private messaging
* Chat history stored on the server
* Server portability across networks

It is designed for use in hostels, schools, labs, and disaster zones, making local communication easy and reliable.

---

## The Problem Statement

In many local setups, internet may not be available or reliable. Users in schools, hostels, or offices often need:

* Offline communication with peers
* Group-based discussions
* Private messaging without third-party platforms
* Persistent chat history

Existing solutions usually require an internet connection, leaving a gap for fully offline communication.

---

## The Solution

**LANChat** solves this problem by:

* Allowing users to chat via direct messages entirely over a LAN
* Enabling real time communication by nitifying the user when someone else wants to initiate conversation
* Storing chat history on the server so that all messages are preserved
* Allowing the server to be portable across networks without losing data

Think of it as a private, offline messaging platform that works reliably wherever the local network exists.

---

## Technical Details

**Technologies/Components Used:**

* **Frontend:** Java Swing (GUI)
* **Backend:** Java with Socket Programming
* **Database:** MySql

**Implementation:**

* Server handles multiple clients throught multi-threading
* Clients connect using local IP to a particular port
* Chat history persists on the server even if it is moved to a different network

---

## Screenshots

**1. Home / Login Page**
Allows users to enter a username and connect to the server.


**2. Chat Window**
Shows multiple groups, allows sending messages, and supports private messaging.

**3. Server & Chat History**
Server stores chat history which remains accessible even if moved across networks.


---

