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

## Known limitations

This is the classic textbook solution, so it inherits the classic textbook problems:

- **Writer starvation.** Since any new reader can join in as long as at least one reader already holds `S`, a steady stream of readers can keep a waiting writer blocked indefinitely. A fairer version would need something like a ticket/turnstile semaphore so writers get queued in order instead of being shut out by readers.
- **`readers` isn't thread-safe on its own** — it's a plain `int`, and the only reason the increment/decrement in `readLock`/`readUnLock` is safe is that it's always done while holding `mutex`. That's correct here, but it's easy to break by accident if someone touches `readers` outside that lock later.
- **No clean shutdown.** `Writer`/`Reader` loop with `while (true)`, so `executorService.shutdown()` doesn't actually stop the already-running tasks — it just stops accepting new ones. Fine for a short demo, not fine for anything long-running.
- **Fixed thread counts.** 4 readers and 4 writers are hardcoded in `main`; there's no way to configure this without editing the source.
