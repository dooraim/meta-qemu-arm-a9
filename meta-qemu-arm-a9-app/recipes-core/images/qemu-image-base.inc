inherit core-image
inherit image-buildinfo

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_INSTALL_append = "\
	bash \
    helloworld \
    python3 \
    python3-pudb \
"

IMAGE_FSTYPES = "tar.bz2"
