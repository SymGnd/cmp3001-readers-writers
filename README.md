# Readers-Writers Problem (Semaphore-Based)

A Java solution to the classic readers-writers synchronization problem, written for an Operating Systems course project (CMP3001).

## The problem

Multiple threads share a resource: some only read it, some write to it. Reads can happen concurrently since they don't change anything, but a write must have exclusive access — no other reader or writer can touch the resource while one is writing.

## Approach

`ReadWriteLock` coordinates access using two semaphores:

- **`S`** — a binary semaphore guarding the shared resource itself. A writer holds it for the entire duration of the write. Readers only need to hold it while the *first* reader arrives, since after that other readers can join in freely.
- **`mutex`** — a binary semaphore protecting the `readers` counter, so increments/decrements from different reader threads don't race with each other.

The first reader to arrive acquires `S`, blocking out any writers; the last reader to leave releases it. Readers in between don't touch `S` at all, so any number of them can read at once. Writers always acquire `S` directly, giving them exclusive access.

`ReadWriteLockTest` spins up 4 writer threads and 4 reader threads on a cached thread pool and lets them run concurrently, each printing when it's reading or writing.

## Running it

```bash
javac src/ReadWriteLockTest.java -d out
java -cp out ReadWriteLockTest
```

It runs indefinitely (readers and writers loop forever), so stop it manually once you've seen enough interleaved output.
