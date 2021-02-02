[![CI](https://github.com/wcm-io-devops/ansible-jenkins-script-security/workflows/CI/badge.svg?branch=master&event=push)](https://github.com/wcm-io-devops/ansible-jenkins-script-security/actions?query=workflow%3ACI)

# wcm_io_devops.jenkins_script_security

This role manages the approval of signatures for the
[Script Security Plugin](https://wiki.jenkins.io/display/JENKINS/Script+Security+Plugin).
The role can add or remove signatures by keeping unmanaged signatures in place.

For the management a groovy script is used that is directly executed in
the Jenkins, so the changes are applied immediately without need to
restart the instance.

## Requirements

This role requires Ansible 2.7 or higher and a running Jenkins on the
target instance.

## Role Variables

Available variables are listed below, along with their default values.

    jenkins_script_security_admin_username: admin

Jenkins admin username.

    jenkins_script_security_admin_password: admin

Jenkins admin password.

    jenkins_script_security_jenkins_hostname: localhost

Hostname of the jenkins instance.

    jenkins_script_security_jenkins_port: 8080

HTTP port of the jenkins instance.

    jenkins_script_security_jenkins_url_prefix: ""

Url prefix of the jenkins instance, e.g. when running in tomcat.

    jenkins_script_security_jenkins_update_dir: "{{ jenkins_plugins_jenkins_home }}/updates"

Path to the jenkins update directory.

    jenkins_script_security_jenkins_base_url: "http://{{ jenkins_script_security_jenkins_hostname }}:{{ jenkins_script_security_jenkins_port }}{{ jenkins_script_security_jenkins_url_prefix }}"

The base url of the jenkins instance.

    jenkins_script_security_approved_signatures_present: []

List of signature to approve. List of strings as present in the
scriptApproval.xml but without the encapsulating `<string></string>`.

    jenkins_script_security_approved_signatures_absent: []

List of signatures to remove. List of strings as present in the
scriptApproval.xml but without the encapsulating `<string></string>`.

## Dependencies

The role has no dependencies.

## Example Playbook

Approves the signatures for creating a new ArrayList and getting the
index of an Element in a list.

	- hosts: jenkins
	  vars:
	    jenkins_script_security_approved_signatures_present:
          - method java.util.List indexOf java.lang.Object
          - new java.util.ArrayList
	  roles:
	    - role: wcm_io_devops.jenkins_script_security

## License

Apache 2.0