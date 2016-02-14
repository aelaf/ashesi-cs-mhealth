import sqlite3
import csv


conn = sqlite3.connect('mhealthbackup')
myconn = conn.cursor()

opd_query = "SELECT comunity_members.community_member_id, comunity_members.community_member_name, comunity_members.gender" \
        ", comunity_members.birthdate, opd_cases.opd_case_name, community_members_opd_cases.rec_date " \
        "FROM community_members_opd_cases inner join comunity_members on comunity_members.community_member_id =" \
        "community_members_opd_cases.community_member_id inner join  opd_cases on opd_cases.opd_case_id = " \
        "community_members_opd_cases.opd_case_id"

vaccine_query = "SELECT comunity_members.community_member_id, comunity_members.community_member_name, comunity_members.birthdate," \
                "comunity_members.gender, vaccines.vaccine_name, vaccine_records.vaccine_date from vaccine_records " \
                "inner join vaccines on vaccines.vaccine_id = vaccine_records.vaccine_id inner join comunity_members on" \
                " comunity_members.community_member_id = vaccine_records.community_member_id"
maleCount = 0
femaleCount = 0
count = 1

opd_file = open("vaccine.csv", "wb")
opd_writer = csv.writer(opd_file, delimiter=',')
#opd_writer.writerow(['Community member ID', 'Community member name', ' gender ', 'Birthdate', 'OPD case name',
#                    'opd case rec_date'])
opd_writer.writerow(['Community member ID', 'Community member name', ' birthdate ', 'gender', 'vaccine name',
                    'vaccine date'])
for row in myconn.execute(vaccine_query):
    if row[3] == "female":
        femaleCount += 1
    elif row[3] == "male":
        maleCount += 1
    print row
    print "Writing line ", count, "to file\n"
    opd_writer.writerow(row)

print "Number of males: ", maleCount
print "Number of females: ", femaleCount