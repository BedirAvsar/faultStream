# Contributing to FaultStream

First off, thank you for considering contributing to FaultStream. It's people like you that make FaultStream such a great tool.

## 1. Where do I go from here?

If you've noticed a bug or have a feature request, make one! It's generally best if you get confirmation of your bug or approval for your feature request this way before starting to code.

## 2. Fork & create a branch

If this is something you think you can fix, then fork FaultStream and create a branch with a descriptive name.

A good branch name would be (where issue #325 is the ticket you're working on):

```sh
git checkout -b 325-add-mqtt-adapter
```

## 3. Implement your fix or feature

At this point, you're ready to make your changes. Feel free to ask for help; everyone is a beginner at first.

## 4. Code Standards & Style

* **Java (Backend)**: We use standard Java formatting guidelines. Ensure that your code passes `./mvnw checkstyle:check` (if configured) or follows the basic spacing defined in `.editorconfig`.
* **TypeScript (Frontend)**: We use ESLint. Please ensure `npm run lint` passes without errors.
* **Commit Messages**: We strictly follow [Conventional Commits](https://www.conventionalcommits.org/). Example: `feat(sensor): add temperature support`.

## 5. Make a Pull Request

At this point, you should switch back to your master branch and make sure it's up to date with FaultStream's master branch:

```sh
git remote add upstream git@github.com:bediravsar/faultStream.git
git checkout main
git pull upstream main
```

Then update your feature branch from your local copy of main, and push it!

```sh
git checkout 325-add-mqtt-adapter
git rebase main
git push --set-upstream origin 325-add-mqtt-adapter
```

Finally, go to GitHub and make a Pull Request.

## 6. Keeping your Pull Request updated

If a maintainer asks you to "rebase" your PR, they're saying that a lot of code has changed, and that you need to update your branch so it's easier to merge.

Thank you!
