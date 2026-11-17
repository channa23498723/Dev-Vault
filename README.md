# Dev-Vault
*A lightweight, desktop-based code snippet and notes manager built for Computer Science students and developers.*

---

## Overview
**Dev-Vault** is a desktop productivity application designed to eliminate the hassle of switching between scattered text files, browsers, and code editors for quick syntax references or debugging notes. Built entirely in **Java**, it combines an intuitive graphical interface with core **Data Structures and Algorithms (DSA)** and local **File I/O** to provide a fast, structured workflow for managing technical knowledge.

---

## Tech Stack & Core Data Structures
- **Programming Language:** Java (JDK 8+)
- **GUI Framework:** Java Swing (`JFrame`, `JPanel`, `JTextArea`, `JList`, `JButton`)
- **Data Structures & Algorithms:**
  - **Stacks (`undoStack`, `redoStack`):** Powers multi-step Undo/Redo operations with state snapshots.
  - **`StringBuilder`:** Handles efficient mutable text manipulation ($O(1)$ amortized updates).
  - **`HashMap<String, List<Snippet>>`:** Provides $O(1)$ average-time lookup for tag-based categorization and instant keyword filtering.
- **Persistence:** Java `java.io` (`File`, `BufferedWriter`, `BufferedReader`) for local disk serialization.

---

## System Architecture & Team Modules
To maintain clean separation of concerns and zero merge conflicts, the application is divided into three core modules:

| Module | Lead Developer | Core Responsibilities & Technologies |
| :--- | :--- | :--- |
| **Module 1: Text Editor & Undo/Redo Engine** | **Anam** | Real-time text creation, editing, deletion, and state history tracking using `Stacks` & `StringBuilder`. |
| **Module 2: Snippet Organization & Search Engine** | **Aqsa** | Tag-based categorization (`#java`, `#dsa`, `#sql`), fast indexing, and multi-criteria searching using `HashMap`. |
| **Module 3: Storage Engine & GUI Manager** | **Erum** | Main desktop layout, event listeners (`ActionListener`, `DocumentListener`), and local disk persistence. |

---

## System Workflow
1. **Startup & Load:** Module 3 initializes the local vault directory, loads existing snippets into Module 2's HashMap index, and renders the desktop UI.
2. **Navigation & Selection:** Users select snippets from the sidebar list, filter by category tags, or initiate a new snippet.
3. **Editing & State Tracking:** As the user types, Module 1 updates the buffer and records state snapshots in `undoStack` and `redoStack`.
4. **Tagging & Indexing:** Upon saving, Module 2 updates the tag index and refreshes the sorted UI snippet list.
5. **Disk Persistence:** Module 3 serializes the updated data directly to local disk storage with user confirmation feedback.

---

## Getting Started & Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/your-username/Dev-Vault.git](https://github.com/your-username/Dev-Vault.git)
