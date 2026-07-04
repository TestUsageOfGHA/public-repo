# Practice Task: Order Discount Bug

## Your task
`src/main/java/com/example/practice/OrderService.java` has a bug. Orders of
$100 or more should get a 10% discount; right now every order is charged in
full. Fix `calculateFinalPrice`.

## How submission works
1. Fork this repo (or branch, if you have write access).
2. Edit files under `src/main/**` only.
3. Open a pull request against `main`.
4. A GitHub Action will automatically build and test your submission against
   a hidden test suite and report pass/fail on the PR.

## Rules
- Do not modify anything under `.github/`, or any `Dockerfile` /
  `docker-compose*` file. PRs that touch these paths are automatically
  rejected by CI and require platform-team review even if the check is
  bypassed.
- The test suite is intentionally not visible to you - that's the point.
