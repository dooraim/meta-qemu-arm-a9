DESCRIPTION = "QEMU ARM A9 Devel Image"
LICENSE = "MIT"

inherit core-image
inherit image-buildinfo

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_INSTALL_append = "\
	bash \
    helloworld \
"

IMAGE_FSTYPES = "tar.bz2"
