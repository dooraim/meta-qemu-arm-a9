inherit core-image
inherit image-buildinfo

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_INSTALL_append = "\
	bash \
    helloworld \
    python3 \
    python3-pudb \
    python3-urwid \
    python3-urwid-readline \
"

IMAGE_FSTYPES = "tar.bz2"
