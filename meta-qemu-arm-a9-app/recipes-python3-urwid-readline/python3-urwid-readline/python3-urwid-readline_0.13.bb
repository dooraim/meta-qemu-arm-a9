
SUMMARY = "A textbox edit widget for urwid that supports readline shortcuts"
HOMEPAGE = "https://github.com/rr-/urwid_readline"
AUTHOR = "Marcin Kurczewski <rr-@sakuya.pl>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;md5=c1299891ea67c37cad6e05398ed16b02"

SRC_URI = "https://files.pythonhosted.org/packages/ab/bb/c5b3fec22268d97ad30232f5533d4a5939d4df7ed3917a8d20d447f1d0a7/urwid_readline-0.13.tar.gz"
SRC_URI[md5sum] = "6e0bd170f621f0ad3edab1875bdb5c5f"
SRC_URI[sha256sum] = "018020cbc864bb5ed87be17dc26b069eae2755cb29f3a9c569aac3bded1efaf4"

S = "${WORKDIR}/urwid_readline-0.13"

RDEPENDS_${PN} = ""

inherit setuptools3
