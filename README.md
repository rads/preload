# rads.preload

A companion library for `clojure.tools.deps` to preload dev helper libraries.

## Rationale

Dev helper libraries such as [`hashp`](https://github.com/weavejester/hashp) are easiest to use when preloaded and configured at the user-level. Some build tools support this directly, such as Leiningen and its `:injections` feature. In contrast, the `clojure.tools.deps` API is more limited, focusing on main functions and constructing arguments for them.

We can approximate Leiningen `:injections` by creating a main function that wraps our existing main functions:

```shell
# Without Preload
clojure -M -m nrepl.cmdline --interactive

# With Preload
clojure -M -m rads.preload -m nrepl.cmdline --interactive
```

The `rads.preload/-main` function loads user-level configuration from `~/.clojure/deps.edn`, loads the configured libraries, and calls the original main function.

Once the initial setup is complete, local developer tool configuration is decoupled and can be reused across projects.

## Getting Started

### User-Level Configuration

1. **Add a `:preload` alias to `~/.clojure/deps.edn`:**
    ```clojure
    {:aliases
     {:preload {:extra-deps {zprint/zprint {:mvn/version "1.2.3"}
                             hashp/hashp {:mvn/version "0.2.1"}}
                :rads.preload/namespaces [zprint.core
                                          hashp.core]}}
      :nrepl {:extra-deps {nrepl/nrepl {:mvn/version "0.9.0"}
                           rads/preload {:git/url "git@github.com:rads/preload.git"
                                         :git/sha "..."}}
              :main-opts ["-m" "rads.preload"
                          "-m" "nrepl.cmdline" "--interactive"]}}
    ```

2. **Start a REPL with the following command:**
    ```shell
    clj -M:preload:nrepl
    ```

3. **Now your REPL will have `zprint.core` and `#p` loaded by default:**
    ```clojure
    user=> (do #p (range 5) nil)
    #p[user/eval20379:1] (range 5) => (0 1 2 3 4)
    nil
    user=> (zprint.core/zprint-str (range 5))
    "(0 1 2 3 4)"
    ```

### Project-Level Configuration

Once the `:preload` alias is set up in `~/.clojure/deps.edn`, there are two things you need to add to your project to enable `rads.preload` within your existing app:

1. **Add `"-m" "rads.preload"` to any aliases with `:main-opts` that should include the preloads.**
    ```clojure
    {:aliases
     {:nrepl {:extra-deps {nrepl/nrepl {:mvn/version "0.9.0"}
                           rads/preload {:git/url "https://github.com/rads/preload.git"
                                         :git/sha "..."}}
              :main-opts ["-m" "rads.preload"
                          "-m" "nrepl.cmdline" "--interactive"]}}
    ```

2. **Add `preload` as an alias when using the `clj -M` command.**
    ```shell
    clj -M:preload:nrepl
    ```

3. **Now your REPL will have `zprint.core` and `#p` loaded by default:**
    ```clojure
    user=> (do #p (range 5) nil)
    #p[user/eval20379:1] (range 5) => (0 1 2 3 4)
    nil
    user=> (zprint.core/zprint-str (range 5))
    "(0 1 2 3 4)"
    ```
