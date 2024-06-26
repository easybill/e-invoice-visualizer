# e-invoice-visualizer

![](version-badge.svg)

CII-/XR-Visualizer. Outputs HTML and PDF.

## Build a new version

To build a new version follow these steps:

1. Increase the version number in the `gradle.properties` file.
2. Run `./gradlew createVersionBadge` to create updated version badge.
3. In necessary, run `./gradlew updateXslSubtree` update external dependencies.
4. Push changes to the repository.
5. Create, review and merge a pull request.
6. Create a new release in GitHub with the new version number.