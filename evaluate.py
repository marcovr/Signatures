import evaluation

matrix = evaluation.read_matrix("data/sample.ged")

references = range(0, 10)
ground_truth = [int(x < 5) for x in range(0, 20)]

vector = evaluation.extract_vector(matrix, references)
table = evaluation.create_table(vector, ground_truth)
print("Table:")
for row in table:
    print(row)

print("EER:")
print(evaluation.get_eer(table))
