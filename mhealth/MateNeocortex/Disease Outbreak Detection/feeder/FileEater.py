import os


class FileEater:

    # constructor
    def __init__(self):
        self.uploads_path = "../../uploads/"
        self.archive_path = "../../archives/"

    # change the uploads directory
    def change_uploads_path(self, new_path):
        self.uploads_path = new_path

    # checks if files have been submitted
    def no_uploads(self):
        if os.listdir(self.uploads_path) is None:
            return True
        else:
            return False

    # returns a list of uploaded files
    def get_files(self):
        files_uploaded = []
        for a_file in os.listdir(self.uploads_path):
            if a_file.endswith(".csv"):
                files_uploaded.append(a_file)
        return files_uploaded

    # move a file from uploads to archives
    def send_to_archive(self, filename):
        os.rename(self.uploads_path+filename, self.archive_path+filename)

